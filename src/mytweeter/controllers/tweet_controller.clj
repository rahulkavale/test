(ns mytweeter.controllers.tweet-controller
  (:require [mytweeter.models.tweet :as tweet]
            [mytweeter.models.hashtags :as hashtags])
  (:require [ring.middleware.json :as json]
            [clojure.core.async :as a
             :refer [>! <! chan go]]
            [clojure.tools.logging :as log]))

(defn get-all-tweets []
  {:body {:tweets (tweet/all)}})

(defn create-tweet [tweet]
  (if (tweet/valid? tweet)
    (let [inserted-tweet (tweet/create tweet)]
      (if (map? inserted-tweet)
        (do
          (go (do
                (log/info "putting tweet on hashtag channel")
                (>! hashtags/hashtag-chan inserted-tweet)))
          {:status 200})
        {:status 400 :body {:error "Could not create tweet"}}))))

(defn retweet [tweet-id user]
  (do
    (log/info (str "retweeting tweet " tweet-id " by user " user))
    (let [status (tweet/retweet tweet-id user)]
      (if (string? status)
        {:status 404 :body {:error status}}
        {:status 200}))))

(defn delete-all []
  (tweet/delete-all))

(defn get-retweeters [tweet-id]
  (let [tweet (tweet/get-tweet tweet-id)]
    (do
      (log/info (str "looking up retweeters for " tweet-id))
      (if (nil? tweet)
        {:status 404 :body {:error "Tweet does not exist"}}
        {:status 200 :body {:retweeters (tweet/retweeters-for tweet-id)}}))))
