(ns mario.routes
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [mario.actions :as actions]))

(defroutes app-routes
  (GET "/" [] (actions/cctray))
  (GET "/cc.xml" [] (actions/cctray))

  (PUT "/jobs/:job-name" [job-name] (actions/create-job job-name))

  (POST "/jobs/:job-name/builds"
        [job-name]
        (actions/build job-name))

  (PUT "/jobs/:job-name/builds/:build-index/success"
       [job-name build-index]
       (actions/build-succeeded job-name build-index))

  (PUT "/jobs/:job-name/builds/:build-index/failure"
       [job-name build-index]
       (actions/build-failed job-name build-index))

  (route/not-found "Not Found"))

(def app (handler/site app-routes))

(defn -main [& [port]]
  (jetty/run-jetty (-> #'app-routes (site {}))
                   {:port (Integer. (env :port)) :join? false}))
