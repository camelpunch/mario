(ns mario.routes
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [mario.actions :as actions]))

(defroutes app-routes
  (GET "/cc.xml" [] (actions/cctray))
  (PUT "/jobs/:slug" [slug] (actions/create-job slug))
  (POST "/jobs/:job-id/builds"
        [job-id]
        (actions/build job-id))
  (PUT "/jobs/:job-id/builds/:build-id/failure"
       [job-id build-id]
       {:status 200})
  (route/not-found "Not Found"))

(def app (handler/site app-routes))
