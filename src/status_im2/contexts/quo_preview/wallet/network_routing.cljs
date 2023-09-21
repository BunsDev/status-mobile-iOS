(ns status-im2.contexts.quo-preview.wallet.network-routing
  (:require [quo2.core :as quo]
            [react-native.core :as rn]
            [reagent.core :as reagent]
            [status-im2.contexts.quo-preview.preview :as preview]))

(def descriptor
  [{:label   "Total amount"
    :key     :total-amount
    :type    :select
    :options [{:key 50} {:key 100} {:key 200} {:key 500}]}
   {:label   "Number of networks"
    :key     :networks
    :type    :select
    :options [{:key 2} {:key 3} {:key 4} {:key 5}]}])

(defn- max-amount-kw [number] (keyword (str "max-amount-" number)))

(defn- network-descriptor [network-number]
  (let [display-number (inc network-number)]
    [{:label   (str "(" display-number ") Max amount")
      :key     (max-amount-kw display-number)
      :type    :select
      :options (map #(hash-map :key %) (range 50 210 30))}]))

(defn- descriptor->component-state
  [{:keys [total-amount networks] :as descriptor-state}]
  (fn [idx color]
    {:amount     (/ total-amount networks)
     :max-amount (descriptor-state (max-amount-kw idx))
     :color      color}))

(defn preview
  []
  (let [descriptor-state (reagent/atom {:total-amount 200
                                        :networks     3
                                        :max-amount-0 250
                                        :max-amount-1 250
                                        :max-amount-2 250
                                        :max-amount-3 250
                                        :max-amount-4 250
                                        :max-amount-5 250})]
    (fn []
      (let [descriptor      (->> (range (:networks @descriptor-state))
                                 (mapcat network-descriptor)
                                 (concat descriptor))
            component-state {:total-amount (:total-amount @descriptor-state)
                             :networks     (->> ["#758EEB" "#E76E6E" "#6BD5F0" "#78F06B" "#F0D56B"]
                                                (take (:networks @descriptor-state))
                                                (map-indexed (descriptor->component-state @descriptor-state)))}]
        [preview/preview-container {:state descriptor-state :descriptor descriptor}
         [rn/view {:style {:flex          1
                           :margin-bottom 20}}
          [quo/network-routing component-state]]
         [quo/text (str component-state)]]))))
