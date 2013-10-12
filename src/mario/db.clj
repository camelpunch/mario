(ns mario.db
  (:require [environ.core :refer [env]]
            [taoensso.carmine :as car :refer [wcar]]))

;; Heroku defines redistogo-url
(def server1-conn {:pool {}
                   :spec (if-let [uri (env :redistogo-url)]
                           {:uri uri}
                           (env :redis))})
(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))

(defn wipe [] (wcar* (car/flushdb)))

(defn add-job [job-name & [script]]
  (wcar* (car/hmset "jobs" job-name {:name job-name
                                     :builds []
                                     :script (or script "true")})))

(defn job [job-name]
  "Returns a job by name"
  (first (wcar* (car/hmget "jobs" job-name))))

(defn all-jobs []
  (take-nth 2 (next (wcar* (car/hgetall "jobs")))))

(defn- next-id-for [id-key] (wcar* (car/incr (str "id." id-key))))

(defn build-started [job-name]
  (let [job (job job-name)]
    (wcar*
      (car/hmset "jobs" job-name (assoc job :builds [{:index 1}])))
    1))

(defn build-status [job-name build status]
  (if-let [job (job job-name)]
    (if (>= (count (:builds job)) (Integer. build))
      (wcar*
        (car/hmset "jobs" job-name (assoc job :builds [{:index 1
                                                        :result status}]))))))

