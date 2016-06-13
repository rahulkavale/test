(ns mytweeter.models.user
  (:require [clojure.java.jdbc :as sql])
  (:require [mytweeter.db :as db]))

(defn all []
  (into [] (sql/query db/spec "select * from users")))

(defn valid? [user]
  (not (empty? user )))

(defn insert-user [user]
  (sql/insert! db/spec :users [:first_name] [(get-in user ["user" "first_name"])]))

(defn create [user]
  (try
    (let [[status]  (vec (insert-user user))]
      (= status 1))
    (catch Exception e
      (println (str "got exception " e))
      false)))

(defn create-follower [user-id follower-id]
  (try
    (do
      (sql/insert! db/spec
                   :followers
                   [:user_id :follower_id]
                   [user-id follower-id])
      true)
    (catch Exception e
      (println (str "got exception " e " while create follower"))
      "could not create follower, please try again")))

(defn query-user [user-id]
  (do
    (println (str "looking for user with user-id " user-id ))
    (into [] (sql/query db/spec (str "select * from users where id = " user-id)))))

(defn get-user [user-id]
  (try
    (let [user (query-user user-id)]
      (println "user " user)
      (first user))
    (catch Exception e
      (println (str " go exception " e))
      false)))

(defn follow [user-id follower-id]
  (let [user (get-user user-id)
        follower (get-user follower-id)]
    (cond
      (nil? user) (str "User with id " user-id " does not exist")
      (nil? follower) (str "User with id " follower-id " does not exist")
      :else (create-follower user-id follower-id))))
