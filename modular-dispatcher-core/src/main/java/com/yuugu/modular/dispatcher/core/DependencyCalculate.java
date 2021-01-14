package com.yuugu.modular.dispatcher.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class DependencyCalculate {

    private static final String TAG = "DependencyCalculate";
    private final Map<String, Module> modulesMap;

    private DependencyCalculate(Map<String, Module> modulesMap) {
        this.modulesMap = modulesMap;
    }

    public static DependencyCalculate from(Map<String, Module> modulesMap) {
        if (modulesMap == null) {
            throw new IllegalArgumentException("modulesMap == null");
        }
        return new DependencyCalculate(modulesMap);
    }

    public List<String> calculate() throws IllegalArgumentException {

        final List<Node> nodes = new ArrayList<>();

        Iterator<Map.Entry<String, Module>> entries = modulesMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Module> entry = entries.next();

            Node node = new Node();
            Module module = entry.getValue();

            node.name = entry.getKey();

            if (module.dependsOn() != null) {
                for (String dp : module.dependsOn()) {
                    if (!modulesMap.containsKey(dp)) {
                        throw new IllegalArgumentException("\""+ node.name +"\" depends on : \"" + dp + "\" , but it is not found! " +
                                "【 Maybe you forget to call .registerModule(\""+ dp +"\") while call ModularDispatcher.get().init(server); 】");
                    }
                }
                node.dependsOn.addAll(module.dependsOn());
            }

            nodes.add(node);
        }

        L.i(TAG, "calculate > nodes = " + nodes);

        List<Node> ordered = new ArrayList<>();

        while (!nodes.isEmpty()) {

            L.i(TAG, "calculate > loop... >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            L.i(TAG, "calculate > loop... , current ordered = " + ordered);
            L.i(TAG, "calculate > loop... , current nodes = " + nodes);

            List<Node> zeroNodes = findZeroNodes(nodes);

            if (zeroNodes == null || zeroNodes.size() < 1) {
                throw new IllegalArgumentException("loop dependency found : " + nodes);
            }

            L.i(TAG, "calculate > loop... , zeroNodes = " + zeroNodes);

            for (Node node : zeroNodes) {
                ordered.add(node);
                L.i(TAG, "calculate > handling... , [ordered.add] = " + node);
                removeZeroNodes(nodes, node);
                L.i(TAG, "calculate > handling... , after remove zero , nodes = " + nodes);
            }
        }

        List<String> result = new ArrayList<>();

        L.i(TAG, "calculate > handling finish , ordered = " + ordered);

        for (Node node : ordered) {
            result.add(node.name);
        }

        L.i(TAG, "calculate > handling finish , result = " + result);

        return result;
    }

    private void removeZeroNodes(List<Node> actions, Node zeroAction) {
        Iterator<Node> iterator = actions.iterator();

        L.i(TAG, "removeZeroNodes > nodes = " + actions);
        L.i(TAG, "removeZeroNodes > nodes.size = " + actions.size());
        L.i(TAG, "removeZeroNodes > zeroAction = " + zeroAction);

        while (iterator.hasNext()) {

            Node next = iterator.next();

            L.i(TAG, "  removeZeroNodes > next = " + next);

            if (zeroAction.name.equals(next.name)) {
                iterator.remove();
                L.i(TAG, "  removeZeroNodes > remove self = " + zeroAction);
                continue;
            }

            boolean isRemove = false;
            if (next.dependsOn != null && next.dependsOn.size() > 0) {
                for (String d : next.dependsOn) {
                    if (d.equals(zeroAction.name)) {
                        L.i(TAG, "  removeZeroNodes > before remove other dependency = " + next);
                        next.dependsOn.remove(d);
                        L.i(TAG, "  removeZeroNodes > after remove other dependency = " + next);
                        isRemove = true;
                        break;
                    }
                }
            }

            if (!isRemove) {
                L.i(TAG, "  removeZeroNodes > no need to remove " + next);
            }
        }
    }

    private List<Node> findZeroNodes(List<Node> nodes) {
        final List<Node> result = new ArrayList<>();
        for (Node action : nodes) {
            if (action.dependsOn == null || action.dependsOn.size() < 1) {
                result.add(action);
            }
        }
        return result;
    }

    private static final class Node {

        private String name;
        private final List<String> dependsOn = new ArrayList<>();

        @Override
        public String toString() {
            return "Node {" +
                    "name='" + name + '\'' +
                    ", dependsOn=" + dependsOn +
                    '}';
        }
    }
}

