(ns db-test
  (:require [expectations :refer :all]
            [clojure.set :refer :all]
            [mario.db :refer :all]
            [datomic.api :as d]
            [test-helpers :as t]
            [clj-time.core :as ctime]))

(defn job-uuids [] (repeat (doto (t/uuid) (add :job/name))))

;; naming allows finding
(expect-let [uuid (first (job-uuids))]
            uuid (:job/name (job uuid)))

;; and finding all
(expect-let [uuid (first (job-uuids))]
            uuid (in (map :job/name (all-jobs))))

;; we get nil when finding a name that doesn't exist
(expect nil (job "nonexistent"))

;; can add a build to a job
(expect-let [uuid (first (job-uuids))]
            "first-build"
            (in (do
                  (build-started uuid "first-build")
                  (map :build/name (:job/builds (job uuid))))))

;; can notify about build failure and retrieve the failure
(expect-let [uuid (first (job-uuids))]
            ["failure"] (do
                          (build-started uuid "doomed-build")
                          (build-failed uuid "doomed-build")
                          (map :build/result (:job/builds (job uuid)))))

;; we get nil when failing a non-existent job
(expect nil (build-failed "nonexistentjob" "nonexistentbuild"))

;; or a non-existent build
(expect nil (build-failed (first (job-uuids)) "nonexistentbuild"))
