(ns mytweeter.controllers.tweet-controller
  (:require [mytweeter.models.tweet :as tweet]))

(defn get-all-tweets []
  (tweet/all))
