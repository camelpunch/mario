(ns acceptance-test
  (:require [expectations :refer :all]
            [ring.mock.request :as r]
            [test-helpers :as t]
            [mario.routes :refer [app]]))

(defn GET [path] (app (r/request :get path)))
(defn POST [path] (app (r/request :post path)))
(defn PUT [path & [body]] (app (-> (r/request :put path) (r/body body))))

(defn projects [] (->> (GET "/cc.xml") :body t/parse-cctray-projects))

;; CCTray XML feed 200s
(expect {:status 200} (in (GET "/cc.xml")))

;; triggering a build puts job into Building state
(expect-let [job-name (doto (t/uuid)
                        (#(PUT (str "/jobs/" %) {:name %}))
                        (#(POST (str "/jobs/" % "/builds"))))]
            {:activity "Building"
             :lastBuildStatus "Unknown"}
            (in (t/project-status (projects) job-name)))

;; when someone tells mario that the build completed but failed
;; then the job is a sleeping failure
