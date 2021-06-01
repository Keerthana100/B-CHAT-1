package com.example.callback.b_chat;

public class MessageChatModel {

        private String text;
        private int viewType;

        public MessageChatModel(String text, int viewType) {
            this.text = text;
            this.viewType = viewType;
        }

        public String getText() {
            return text;
        }

        public int getViewType() {
            return viewType;
        }

}
