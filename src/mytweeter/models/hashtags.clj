(ns mytweeter.models.hashtags
  (:require [mytweeter.utils :as utils]))


(defn extract-hashtags-from-string [str]
  (distinct (filter (partial utils/starts-with? \#) (clojure.string/split str #"\s+"))))

(defn extract-hashtags [str-or-tweet]
  (if (string? str-or-tweet)
    (extract-hashtags-from-string str-or-tweet)
    (extract-hashtags-from-string (:body str-or-tweet))))
