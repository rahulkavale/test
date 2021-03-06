(ns mytweeter.controllers.user-controller
  (:require [mytweeter.models.user :as user])
  (:require [clojure.tools.logging :as log]))

(defn get-all-users []
  "returns map containing all the users"
  {:body {:users (user/all)}})

(defn create-user [user]
  "creates a user from given payload containing first_name"
  (if (user/valid? user)
    (let [user-id (user/create user)]
      (cond
        (not (nil? user-id)) {:status 200 :body {:id user-id}}
        :else {:status 400 :error "could not create user"}))))

(defn follow [follow-details]
  "create follow relation between the user and follower given"
  (let [{user-id "user" follower-id "follower"} follow-details]
    (try
      (let [status (user/follow user-id follower-id)]
        (if (string? status)
          {:status 400 :body {:error status}}
          {:status 200}))
      (catch Exception e
        {:status 500 :body {:error "Something went wrong please try again"}}))))

(defn get-user-tweets [user-id]
  "Return the tweets for the user-id given "
  (let [[user] (user/query-user user-id)]
    (if (nil? user)
      {:status 404 :body {:error (str "User with id " user-id " not found")}}
      {:body {:tweets (user/get-tweets user-id)}})))
