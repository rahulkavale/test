(ns mytweeter.controllers.user-controller
  (:require [mytweeter.models.user :as user]))

(defn get-all-users []
  ({:body {:users (user/all)}}))
