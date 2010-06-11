(ns timers.core
  (:use [clojure.contrib.duck-streams :as ds]
        [clojure.contrib.server-socket :as ss])
  (:import [java.io PushbackReader]
           [java.util Date]
           [java.text SimpleDateFormat]))

(def timers (atom {}))

(defn make-timer [length]
  (+ length (System/currentTimeMillis)))

(defn new-timer [command]
  (swap! timers assoc (:name command)
         (make-timer (:length command)))
  (println @timers))

(defn millis-remaining [name]
  (- (@timers name) (System/currentTimeMillis)))

(defn time-left [command]
  (println (.format (SimpleDateFormat. "mm:ss") (millis-remaining (:name command)))))

(def commands {:new-timer new-timer :time-left time-left})

(defn dispatch [command]
  (((:command command) commands) command))

(defn do-stuff [in out]
  (let [command (read (PushbackReader. (ds/reader in)))]
    (binding [*out* (ds/writer out)]
      (dispatch command)
      (flush))))

(defn main-loop []
  (ss/create-server 9899 do-stuff))

(comment
  (def server (main-loop))
  (ss/close-server server)
(* 1000 60 25)
  ( (:command {:command :new-timer}) commands)

  (:new-timer commands)

)



