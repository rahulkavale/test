(ns mytweeter.models.tweet
  (:require [clojure.java.jdbc :as sql]
            [mytweeter.models.user :as user])
  (:require [mytweeter.db :as db]))

(defn all []
  (into [] (sql/query db/spec "select * from tweets")))

(defn valid? [tweet]
  (and (not (empty? tweet ))
       (not (nil? (get tweet "user_id")))
       (not (nil? (get tweet "body")))))

(defn insert-tweet [tweet]
  (vec (sql/insert! db/spec
                    :tweets
                    [:body :user_id]
                    [(get tweet "body") (read-string (get tweet "user_id"))])))

(defn create [tweet]
  (try
    (let [[status] (insert-tweet tweet)]
      (= status 1))
    (catch Exception e
      (println (str "got exception " e))
      false)))

;TODO better way for this
(defn delete-all []
  (sql/delete! db/spec :tweets ["true = ? " true]))

(defn get-tweet [tweet-id]
  (do
    (println (str "looking up tweet with id" tweet-id))
    (first (into [] (sql/query db/spec (str "select * from tweets where id = " tweet-id))))))

(defn insert-retweet [tweet user]
  (try
    (sql/insert! db/spec
                 :retweets
                 [:user_id :tweet_id]
                 [(:id user) (:id tweet)])
    true
    (catch Exception e
      (println (str "got exception " e " while retweeting"))
      "could not retweet, please try again")))

(defn retweet [tweet-id user]
  (try
    (let [user (user/get-user (get user "user_id"))
          tweet (get-tweet tweet-id)]
      (cond
        (nil? user) "User does not exists"
        (nil? tweet) "Tweet does not exist"
        :else (insert-retweet tweet user))))) 


(defn retweeters-for [tweet-id]
  (try
    (into [] (sql/query db/spec (str "select user_id, created_at from retweets"
                                     " where tweet_id = " tweet-id)))
    (catch Exception e
      (println (str "could not get retweeters, got exception " e))
      [])))
