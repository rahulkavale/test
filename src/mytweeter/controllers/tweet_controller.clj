(ns mytweeter.controllers.tweet-controller
  (:require [mytweeter.models.tweet :as tweet]
            [mytweeter.models.hashtags :as hashtags])
  (:require [ring.middleware.json :as json]
            [clojure.core.async :as a
             :refer [>! <! chan go]]
            [clojure.tools.logging :as log]))

(defn get-all-tweets []
  "Get all the tweets"
  {:body {:tweets (tweet/all)}})

(defn create-tweet [tweet]
  "Create tweet and publish the tweet onto hashtags channel for getting trending hashtags"
  (if (tweet/valid? tweet)
    (let [inserted-tweet (tweet/create tweet)]
      (if (map? inserted-tweet)
        (do
          (go (log/info "putting tweet on hashtag channel")
              (>! hashtags/hashtag-chan inserted-tweet))
          {:status 200})
        {:status 400 :body {:error "Could not create tweet"}}))))

(defn retweet [tweet-id user]
  "retweet the given tweet by the user specified, the user format is 
   {\"user_id\": \"1235\"}
  "
  (log/info (str "retweeting tweet " tweet-id " by user " user))
  (let [status (tweet/retweet tweet-id user)]
    (if (string? status)
      {:status 404 :body {:error status}}
      {:status 200})))

(defn delete-all []
  "Delete all the tweets"
  (tweet/delete-all))

(defn get-retweeters [tweet-id]
  "Returns list of retweeters for the given tweet id, returns empty
   list if none exist"
  (let [tweet (tweet/get-tweet tweet-id)]
    (log/info (str "looking up retweeters for " tweet-id))
    (if (nil? tweet)
      {:status 404 :body {:error "Tweet does not exist"}}
      {:status 200 :body {:retweeters (tweet/retweeters-for tweet-id)}})))
