(ns mario.routes
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [mario.views :as views]))

(defroutes app-routes
           (GET "/" []
                (views/index-page))
           (route/resources "/")
           (route/not-found "Not Found"))

(def app (handler/site app-routes))
