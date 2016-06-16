(ns mytweeter.models.user
  (:require [clojure.java.jdbc :as sql]
            [clojure.tools.logging :as log])
  (:require [mytweeter.db :as db]))

(defn all []
  "get all the users"
  (into [] (sql/query @db/config "select * from users")))

(defn valid? [user]
  "check if the user is valid ie non empty"
  (and (not (empty? user ))
       (not (nil? (get-in user ["user" "first_name"])))))

(defn insert-user [user]
  "executes sql to insert the user record into user database table"
  (sql/insert! @db/config :users {:first_name (get-in user ["user" "first_name"])}))

(defn create [user]
  "Create a user given map of user details
   returns boolean indicating success or failure of the opreation"
  (try
    (log/info "Creating user with payload " user)
    (let [user-map (insert-user user)]
      (:id (first user-map)))
    (catch Exception e
      (log/error (str "got exception " e))
      nil)))

(defn create-follower [user-id follower-id]
  "Create database record for the given follower to the given user"
  (try
    (sql/insert! @db/config
                 :followers
                 [:user_id :follower_id]
                 [user-id follower-id])
    true
    (catch Exception e
      (log/error (str "got exception " e " while create follower"))
      "could not create follower, please try again")))

(defn query-user [user-id]
  "function to query database to get the user given a user id"
  (log/info (str "looking for user with user-id " user-id ))
  (into [] (sql/query @db/config (str "select * from users where id = " user-id))))

(defn get-tweets [user-id]
  "Returns tweets for a given user-id, returns empty vecotr if none found"
  (try
   (into [] (sql/query @db/config (str "select * from tweets where "
                                    "user_id = " user-id)))
   (catch Exception e
     (log/error (str "Got exception" e)
              []))))

(defn get-user [user-id]
  "Get a user from database if user exists with given user-id or else return false"
  (try
    (let [user (query-user user-id)]
      (first user))
    (catch Exception e
      (log/error (str " go exception " e))
      false)))

(defn follows? [user-id follower-id]
  "Check if a given followe-id follows the given user-id"
  (not (empty? (into [] (sql/query @db/config
                               (str "select * from followers where user_id = " user-id
                                    " and follower_id = " follower-id))))))

(defn follow [user-id follower-id]
  "Associate given follower-id and user-id into a follows relation"
  (let [user (get-user user-id)
        follower (get-user follower-id)]
    (cond
      (nil? user) (str "User with id " user-id " does not exist")
      (nil? follower) (str "User with id " follower-id " does not exist")
      (follows? (read-string user-id) (read-string follower-id))
      (str "User " follower-id " already follows user " user-id)
      :else (create-follower (read-string user-id) (read-string follower-id)))))
