(ns actions-test
  (:require [expectations :refer :all]
            [test-helpers :as t]
            [mario.actions :as actions]))

(defn projects [] (t/parse-cctray-projects (:body (actions/cctray))))

;; Creating without a name 200s
(expect {:status 200} (actions/create-job (t/uuid)))

;; Building 201s with location when job exists
(expect-let [job-name (doto (t/uuid) actions/create-job)]
            {:status 201
             :headers {"Location" (str "/jobs/" job-name "/builds/1")}}
            (actions/build job-name))

;; ...but 404s for job that doesn't exist
(expect {:status 404} (actions/build "non-existent-job"))

;; Job appears in feed after build is triggered for first time
(expect-let [job-name (doto (t/uuid) actions/create-job actions/build)]
            {:name job-name
             :activity "Building"
             :lastBuildStatus "Unknown"}
            (t/project-status (projects) job-name))

