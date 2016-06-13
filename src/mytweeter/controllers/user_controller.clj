(ns mytweeter.controllers.user-controller
  (:require [mytweeter.models.user :as user]))

(defn get-all-users []
  {:body {:users (user/all)}})

(defn create-user [user]
  (if (user/valid? user)
    (let [status (user/create user)]
      (cond
        (= status true) {:status 200}
        :else {:status 400 :error "could not create user"}))))


(defn follow [follow-details]
  (let [{user-id "user" follower-id "follower"} follow-details]
    (try
      (let [status (user/follow user-id follower-id)]
        (if (string? status)
          {:status 400 :body {:error status}}
          {:status 200}))
      (catch Exception e
        {:status 500 :body {:error "Something went wrong please try again"}}))))

(defn get-user-tweets [user-id]
  (let [[user] (user/query-user user-id)]
    (do
      (println user-id)
     (if (nil? user)
       {:status 404 :body {:error (str "User with id " user-id " not found")}}
       {:body {:tweets (user/get-tweets user-id)}}))))
