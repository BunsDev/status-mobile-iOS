(ns quo2.components.wallet.network-routing.view
  (:require
   [quo2.foundations.colors :as colors]
   [react-native.core :as rn]
   [react-native.reanimated :as reanimated]
   [reagent.core :as reagent]))

(defn slider []
  []
  [rn/view {:style {:width           8
                    :justify-content :center
                    :align-items     :center
                    :right           -4}}
   [reanimated/view
    {:style {:background-color colors/white
             :height           32
             :width            4
             :border-radius    4}}]])

(defn network-bar []
  (let [show-slider? (reagent/atom false)]
    (fn [{:keys [max-width selected-width]}]
      [reanimated/view {:style {:width            (if @show-slider?
                                                    max-width
                                                    selected-width)
                                :background-color "#758EEB40"
                                :flex-direction   :row
                                :justify-content  :space-between}}

       [rn/touchable-without-feedback {:on-press #(swap! show-slider? not)}
        [reanimated/view {:style {:width            selected-width
                                  :flex-direction   :row
                                  :justify-content  :flex-end
                                  :background-color "#758EEB"}}
         (when @show-slider?
           [slider])]]

       (when @show-slider?
         [reanimated/view
          {:style {:border-left-width 1
                   :border-style      :dashed
                   :border-color      "#758EEB"}}])
       ]
      )))

(defn view []
  [rn/view {:style { ;:margin-horizontal 20
                    :flex-direction    :row
                    :height            64
                    :background-color  :lightgrey
                    :border-radius     18 ;; TODO: check border-radius
                    :overflow          :hidden}}
   [:f> network-bar {:max-width      160
                     :selected-width 120}]

   #_#_#_#_#_[rn/view {:style {:width            "33.3%"
                               :background-color :blue}}]
           [rn/view {:style {:width 1}}]
           [rn/view {:style {:width            "33.3%"
                             :background-color :red}}]
           [rn/view {:style {:width 1}}]
           [rn/view {:style {:width            "33.3%"
                             :background-color :skyblue}}]
   ])
