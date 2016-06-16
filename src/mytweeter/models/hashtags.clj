(ns mytweeter.models.hashtags
  (:require [mytweeter.utils :as utils]
            [clojure.java.jdbc :as sql]
            [mytweeter.db :as db]
            [clojure.core.async
             :as a
             :refer [>! >!! <! go chan go-loop]]
            [mytweeter.db :as db]
            [mytweeter.models.trending :as trending]
            [clojure.tools.logging :as log]))

(def hashtag-chan (chan 2))

(defn extract-hashtags-from-string [str]
  "Given a string returns all unique the hashtags contained in the string"
  (vec (distinct (filter (partial utils/starts-with? \#) (clojure.string/split str #"\s+")))))

(defn extract-hashtags [str-or-tweet]
  "Extracts hashtags from a tweet string or a tweet map with :body in it"
  (log/info "extracting hashtags from " str-or-tweet)
  (if (string? str-or-tweet)
    (extract-hashtags-from-string str-or-tweet)
    (extract-hashtags-from-string (:body str-or-tweet))))

(defn get-hashtag [body]
  "Get a hashtag by its content, returns nil if nothing found"
  (first (into [] (sql/query @db/config (str "select * from hashtags where body = '" body "'")))))

(defn get-or-create [hashtag-body]
  "If a hashtag already exists with the content given, the
   corresponding db record is returnined, otherwise
   new record is created and returned"
  (let [existing (get-hashtag hashtag-body)]
    (if (empty? existing)
      (sql/insert! @db/config
                   :hashtags
                   {:body hashtag-body})
      existing)))


;TODO check if hashtag already present else insert
(defn save-hashtags [hashtags]
  "Insert hashtags into db, the parameter is hashtag body content list"
  (flatten (map get-or-create hashtags)))

(defn insert-tweet-hashtag [tweet hashtag]
  "Insert a tweet and its associated hashtag
   takes tweet map and hashtag map with id"
  (log/info "inserting " tweet " and " hashtag)
  (sql/insert! @db/config
               :tweet_hashtags
               {:tweet_id (:id tweet) :hashtag_id (:id hashtag)}))

(defn associate-hashtags [tweet hashtags]
  "Associates multiple hashtags to a tweet object by creating corresponding
   hashtag records into db"
  (log/info "associating hashtags " hashtags " with " tweet)
  (let [saved-hashtags (save-hashtags hashtags)]
    (doall (map #(insert-tweet-hashtag tweet %) saved-hashtags))))

(defn process-hashtags []
  "Whenever a new tweet is created, we process the tweet for retrieving
   hashtags from it and associate the same with the tweet
   this is used to do the trending hashtag calculation"
  (go-loop []
    (let [tweet (<! hashtag-chan)
          hashtags (extract-hashtags tweet)]
      (associate-hashtags tweet hashtags)
      ;;TODO map does not wor
      ;; (doall (map (fn [h] (>! trending/trending-hashtags h)) hashtags))
      (doseq [h hashtags] (>! trending/trending-hashtags-chan h)))
    (recur)))
