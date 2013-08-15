(ns mario.routes
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [mario.actions :as actions]))

(defroutes app-routes
  (GET "/" [] (actions/cctray))
  (GET "/cc.xml" [] (actions/cctray))

  (PUT "/jobs/:job-name" [job-name] (actions/create-job job-name))

  (POST "/jobs/:job-name/builds"
        [job-name]
        (actions/build job-name))

  (PUT "/jobs/:job-name/builds/:build-name/failure"
       [job-name build-name]
       (actions/build-failed job-name build-name))

  (route/not-found "Not Found"))

(def app (handler/site app-routes))
