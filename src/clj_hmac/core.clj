(ns clj-hmac.core
  (:import [java.nio ByteBuffer]
           [javax.crypto Mac KeyGenerator]
           [javax.crypto.spec SecretKeySpec]))


(def ^:dynamic *default-algorithm* "HmacMD5")
(def ^:dynamic *hash-algorithms*
  {"HmacMD5" "md5"})

(defn hmac-algorithm
  "Return a canonical string representing the hash algorithm."
  [& alg]
  (get *hash-algorithms* alg *default-algorithm*))

(defprotocol IHmacAble
  (hmac [this key alg] [this key]))

(extend-protocol IHmacAble
  (class (byte-array []))
  (hmac
    ([this key algorithm]
       (-> (doto (Mac/getInstance algorithm) (.init (SecretKeySpec. key algorithm)))
           (.doFinal this)))
    ([this key] (hmac this key *default-algorithm*)))

  String
  (hmac
    ([this key algorithm]
       (hmac (.getBytes this "UTF-8") key algorithm))
    ([this key]
       (hmac (.getBytes this "UTF-8") key)))

  ByteBuffer
  (hmac
    ([this key algorithm]
       (let [bytes (.remaining this)]
         (doto this (.get bytes) .reset)
         (hmac bytes key algorithm)))
    ([this key]
       (hmac this key *default-algorithm*))))
