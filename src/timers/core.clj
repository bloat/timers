(ns timers.core
  (:use [clojure.contrib.duck-streams :as ds]
        [clojure.contrib.server-socket :as ss])
  (:import [java.io PushbackReader]))

(def timers (atom {}))

(defn make-timer [length]
  (+ length (System/currentTimeMillis)))

(defn new-timer [command]
  (swap! timers assoc (:name command)
         (make-timer (:length command))))

(def commands {:new-timer new-timer})

(defn dispatch [command]
  (((:command command) commands) command))

(defn do-stuff [in out]
  (let [command (read (PushbackReader. (ds/reader in)))]
    (dispatch command)))

(defn main-loop []
  (ss/create-server 9899 do-stuff))

(comment
  (def server (ss/create-server 9899 do-stuff))
  (ss/close-server server)

  ( (:command {:command :new-timer}) commands)

  (:new-timer commands)
  )



