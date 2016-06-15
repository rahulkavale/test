(ns mytweeter.models.trending
  (:require [clojure.core.async :as a
             :refer [>! <! chan go-loop]]
            [mytweeter.utils :as utils]
            [clojure.tools.logging :as log]))

(def trending-hashtags-chan (chan 2))

(def trending-hashtags (atom {}))

(defn process-trending-hashtags []
  "listens to channle where hashtags are pushed
   keeps a occurance count for each of the hashtag"
  (go-loop []
    (let [hashtag (<! trending-hashtags-chan)]
      (log/info "found hahstag " hashtag " for trending calculation")
      (swap! trending-hashtags update-in [hashtag] (fnil inc 0) )
      (log/info "map is now " @trending-hashtags))
    (recur)))

