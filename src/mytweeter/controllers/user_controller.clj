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
        (cond
          (string? status) {:status 400 :body {:error status}}
          :else {:status 200}))
      (catch Exception e
        {:status 500 :body {:error "Something went wrong please try again"}}))))
