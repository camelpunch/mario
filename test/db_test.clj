(ns db-test
  (:require [expectations :refer :all]
            [clojure.set :refer :all]
            [mario.db :refer :all]
            [test-helpers :as t]
            [clj-time.core :as ctime]))

(defn job-uuids [] (repeat (doto (t/uuid) add-job)))

;; job without name can be retrieved
;; - defaults to a no-op script
;; - has no builds
(expect-let [job-name (first (job-uuids))]
            {:name job-name
             :script "true"
             :builds []}
            (job job-name))

;; and finding all
(expect-let [job-name (first (job-uuids))]
            job-name (in (seq (map :name (all-jobs)))))

;; can store script against a job name
(expect "my awesome script"
        (let [uuid (t/uuid)]
          (add-job uuid "my awesome script")
          (:script (job uuid))))

;; we get nil when finding a name that doesn't exist
(expect nil (job "nonexistent"))

;; can add a build to a job
(expect {:index 1}
        (let [uuid (first (job-uuids))]
          (build-started uuid)
          (first (:builds (job uuid)))))

;; can notify about build status and retrieve the status from single job
(expect {:index 1 :result "some-status"}
        (let [uuid (first (job-uuids))
              build-id (str (build-started uuid))]
          (build-status uuid build-id "some-status")
          (last (:builds (job uuid)))))

;; or from all jobs
(expect {:result "failure"}
        (in (let [uuid (first (job-uuids))
                  build-id (build-started uuid)]
              (build-status uuid build-id "failure")
              (first (:builds
                       (first
                         (filter #(= uuid (:name %)) (all-jobs))))))))

;; we get nil when changing status of a non-existent job
(expect nil (build-status "nonexistentjob" 0 "great-status"))

;; or a non-existent build
(expect nil (build-status (first (job-uuids)) 999999 "ace-status"))
