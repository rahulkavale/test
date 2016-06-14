(ns mytweeter.models.hashtags
  (:require [mytweeter.utils :as utils]
            [clojure.java.jdbc :as sql]
            [mytweeter.db :as db]
            [clojure.core.async
             :as a
             :refer [>! <! go chan go-loop]]
            [mytweeter.db :as db]))

(def hashtag-chan (chan 2))

(defn extract-hashtags-from-string [str]
  (vec (distinct (filter (partial utils/starts-with? \#) (clojure.string/split str #"\s+")))))

(defn extract-hashtags [str-or-tweet]
  (do
    (println "extracting hashtags from " str-or-tweet)
    (if (string? str-or-tweet)
      (extract-hashtags-from-string str-or-tweet)
      (extract-hashtags-from-string (:body str-or-tweet)))))

;TODO calculate trending hashtags

(defn get-hashtag [body]
  (first (into [] (sql/query db/spec (str "select * from hashtags where body = '" body "'")))))

(defn get-or-create [hashtag]
  (let [existing (get-hashtag hashtag)]
    (if (empty? existing)
      (sql/insert! db/spec
                   :hashtags
                   {:body hashtag})
      existing)))


;TODO check if hashtag already present else insert
(defn save-hashtags [hashtags]
  (flatten (map get-or-create hashtags)))

(defn insert-tweet-hashtag [tweet hashtag]
  (do
    (println "inserting " tweet " and " hashtag)
    (sql/insert! db/spec
                 :tweet_hashtags
                 {:tweet_id (:id tweet) :hashtag_id (:id hashtag)})))

(defn associate-hashtags [tweet hashtags]
  (do
    (println "associating hashtags " hashtags " with " tweet)
    (let [saved-hashtags (save-hashtags hashtags)]
      (doall (map #(insert-tweet-hashtag tweet %) saved-hashtags)))))

(defn process-hashtags []
  (go
    (loop []
      (let [tweet (<! hashtag-chan)
            hashtags (extract-hashtags tweet)]
        (do
          (println " hashtags " hashtags)
          (associate-hashtags tweet hashtags)))
     (recur ))))
