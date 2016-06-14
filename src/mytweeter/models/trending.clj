(ns mytweeter.models.trending
  (:require [clojure.core.async :as a
             :refer [>! <! chan go-loop]]
            [mytweeter.utils :as utils]))

(def trending-hashtags-chan (chan 2))

(def trending-hashtags (atom {}))

(defn process-trending-hashtags []
  (go-loop []
    (let [hashtag (<! trending-hashtags-chan)]
      (println "found hahstag " hashtag " for trending calculation")
      (swap! trending-hashtags update-in [hashtag] (fnil inc 0) )
      (println "map is now " @trending-hashtags))
    (recur)))

