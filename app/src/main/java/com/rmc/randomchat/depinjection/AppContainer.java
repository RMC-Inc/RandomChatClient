package com.rmc.randomchat.depinjection;

import com.rmc.randomchat.net.RandomChatRepository;
import com.rmc.randomchat.net.RandomChatRepositoryImpl;

public class AppContainer {
    public final RandomChatRepository randomChatRepository = new RandomChatRepositoryImpl();
}
