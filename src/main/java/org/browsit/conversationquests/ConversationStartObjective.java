/*
 * Copyright (c) 2024 Browsit, LLC. All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.browsit.conversationquests;

import fr.supermax_8.conversation.blueprints.Conversation;
import fr.supermax_8.conversation.event.ConversationStartEvent;
import me.pikamug.quests.enums.ObjectiveType;
import me.pikamug.quests.module.BukkitCustomObjective;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.AbstractMap;
import java.util.Map;

public class ConversationStartObjective extends BukkitCustomObjective implements Listener {

    public ConversationStartObjective() {
        setName("Conversation Start Objective");
        setAuthor("Browsit, LLC");
        setItem("PAPER", (short)0);
        setShowCount(true);
        addStringPrompt("Conversation Start Obj", "Set a name for the objective", "Start conversation");
        addStringPrompt("Conversation Start Name", "Enter conversation name", "ANY");
        setCountPrompt("Set the number of times to start the conversation");
        setDisplay("%Conversation Start Obj%: %count%");
    }

    @Override
    public String getModuleName() {
        return "Conversation Quests Module";
    }

    @Override
    public Map.Entry<String, Short> getModuleItem() {
        return new AbstractMap.SimpleEntry<>("PAPER", (short)0);
    }

    @EventHandler
    public void conversationStart(final ConversationStartEvent event) {
        final Player starter = event.getPlayer();
        final Quester quester = ConversationModule.getQuests().getQuester(starter.getUniqueId());
        if (quester == null) {
            return;
        }
        for (final Quest quest : quester.getCurrentQuests().keySet()) {
            final Map<String, Object> dataMap = getDataForPlayer(starter.getUniqueId(), this, quest);
            if (dataMap != null) {
                final String convName = (String)dataMap.getOrDefault("Conversation Start Name", "ANY");
                if (convName == null) {
                    return;
                }
                final Conversation conv = event.getConversation();
                if (convName.equals("ANY") || convName.equalsIgnoreCase(conv.getName())) {
                    incrementObjective(starter.getUniqueId(), this, quest, 1);

                    quester.dispatchMultiplayerEverything(quest, ObjectiveType.CUSTOM,
                            (final Quester q, final Quest cq) -> {
                                incrementObjective(q.getUUID(), this, quest, 1);
                                return null;
                            });
                    return;
                }
                return;
            }
        }
    }
}
