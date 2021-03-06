/**
 * This file is part of Artemis, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 SpongePowered <http://spongepowered.org/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.artemis.managers;

import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.utils.Bag;
import com.artemis.utils.ImmutableBag;

import java.util.HashMap;
import java.util.Map;

/**
 * If you need to group your entities together, e.g. tanks going into "units" group or explosions into "effects",
 * then use this manager. You must retrieve it using world instance.
 * <p/>
 * A entity can be assigned to more than one group.
 *
 * @author Arni Arent
 */
public class GroupManager extends Manager {
    private Map<String, Bag<Entity>> entitiesByGroup;
    private Map<Entity, Bag<String>> groupsByEntity;

    public GroupManager() {
        entitiesByGroup = new HashMap<String, Bag<Entity>>();
        groupsByEntity = new HashMap<Entity, Bag<String>>();
    }

    @Override
    protected void initialize() {
    }

    /**
     * Set the group of the entity.
     *
     * @param group group to add the entity into.
     * @param e     entity to add into the group.
     */
    public void add(Entity e, String group) {
        Bag<Entity> entities = entitiesByGroup.get(group);
        if (entities == null) {
            entities = new Bag<Entity>();
            entitiesByGroup.put(group, entities);
        }
        entities.add(e);

        Bag<String> groups = groupsByEntity.get(e);
        if (groups == null) {
            groups = new Bag<String>();
            groupsByEntity.put(e, groups);
        }
        groups.add(group);
    }

    /**
     * Remove the entity from the specified group.
     *
     * @param e
     * @param group
     */
    public void remove(Entity e, String group) {
        Bag<Entity> entities = entitiesByGroup.get(group);
        if (entities != null) {
            entities.remove(e);
        }

        Bag<String> groups = groupsByEntity.get(e);
        if (groups != null) {
            groups.remove(group);
        }
    }

    public void removeFromAllGroups(Entity e) {
        Bag<String> groups = groupsByEntity.get(e);
        if (groups != null) {
            for (int i = 0; groups.size() > i; i++) {
                Bag<Entity> entities = entitiesByGroup.get(groups.get(i));
                if (entities != null) {
                    entities.remove(e);
                }
            }
            groups.clear();
        }
    }

    /**
     * Get all entities that belong to the provided group.
     *
     * @param group name of the group.
     * @return read-only bag of entities belonging to the group.
     */
    public ImmutableBag<Entity> getEntities(String group) {
        Bag<Entity> entities = entitiesByGroup.get(group);
        if (entities == null) {
            entities = new Bag<Entity>();
            entitiesByGroup.put(group, entities);
        }
        return entities;
    }

    /**
     * @param e entity
     * @return the groups the entity belongs to, null if none.
     */
    public ImmutableBag<String> getGroups(Entity e) {
        return groupsByEntity.get(e);
    }

    /**
     * Checks if the entity belongs to any group.
     *
     * @param e the entity to check.
     * @return true if it is in any group, false if none.
     */
    public boolean isInAnyGroup(Entity e) {
        return getGroups(e) != null;
    }

    /**
     * Check if the entity is in the supplied group.
     *
     * @param group the group to check in.
     * @param e     the entity to check for.
     * @return true if the entity is in the supplied group, false if not.
     */
    public boolean isInGroup(Entity e, String group) {
        if (group != null) {
            Bag<String> groups = groupsByEntity.get(e);
            for (int i = 0; groups.size() > i; i++) {
                String g = groups.get(i);
                if (group == g || group.equals(g)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deleted(Entity e) {
        removeFromAllGroups(e);
    }
}
