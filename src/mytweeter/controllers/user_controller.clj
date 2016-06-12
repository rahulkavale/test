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
