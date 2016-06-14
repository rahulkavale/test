(ns mytweeter.models.hashtags
  (:require [mytweeter.utils :as utils]
            [clojure.core.async
             :as a
             :refer [>! <! go chan go-loop]]))

(def hashtag-chan (chan 2))

(defn extract-hashtags-from-string [str]
  (distinct (filter (partial utils/starts-with? \#) (clojure.string/split str #"\s+"))))

(defn extract-hashtags [str-or-tweet]
  (do
    (println "extracting hashtags from " str-or-tweet)
    (if (string? str-or-tweet)
      (extract-hashtags-from-string str-or-tweet)
      (extract-hashtags-from-string (get str-or-tweet "body")))))

(defn process-hashtags []
  (go
    (loop []
     (when-let [tweet (<! hashtag-chan)]
       (println (str "reecived tweet fot extracting hashtags " tweet ))
       (println (str (vec (extract-hashtags tweet)))))
     (recur ))))
