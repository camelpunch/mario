(ns mario.routes
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [mario.actions :as actions]
            [environ.core :refer [env]]))

(defroutes app
  (GET "/cc.xml" [] (actions/cctray))

  (PUT "/jobs/:job-name" [job-name] (actions/create-job job-name))

  (POST "/jobs/:job-name/builds"
        [job-name]
        (actions/build job-name))

  (PUT "/jobs/:job-name/builds/:build-name/failure"
       [job-name build-name]
       (actions/build-failed job-name build-name))

  (route/not-found "Not Found"))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))
        ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
        store (cookie/cookie-store {:key (env :session-secret)})]
    (jetty/run-jetty (-> #'app
                         ((if (env :production)
                            wrap-error-page
                            trace/wrap-stacktrace))
                         (site {:session {:store store}}))
                     {:port port :join? false})))

