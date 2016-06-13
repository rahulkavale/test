(ns mytweeter.controllers.tweet-controller
  (:require [mytweeter.models.tweet :as tweet])
  (:require [ring.middleware.json :as json]))

(defn get-all-tweets []
  {:body {:tweets (tweet/all)}})

(defn create-tweet [tweet]
  (if (tweet/valid? tweet)
    (let [status (tweet/create tweet)]
      (cond
        (= status true) {:status 200}
        :else {:status 400 :body {:error "Could not create tweet"}}))))

(defn retweet [tweet-id user]
  (do
    (println (str "retweeting tweet " tweet-id " by user " user))
    (let [status (tweet/retweet tweet-id user)]
      (if (string? status)
        {:status 404 :body {:error status}}
        {:status 200}))))

(defn delete-all []
  (tweet/delete-all))

