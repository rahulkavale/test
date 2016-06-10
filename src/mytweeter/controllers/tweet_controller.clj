(ns mytweeter.controllers.tweet-controller
  (:require [mytweeter.models.tweet :as tweet])
  (:require [ring.middleware.json :as json]))

(defn get-all-tweets []
  {:body {:tweets (tweet/all)}})
