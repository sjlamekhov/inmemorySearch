package search.inmemory;

import objects.AbstractObjectUri;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class TrieNode <U extends AbstractObjectUri> {

    private final String value;
    private Map<String, TrieNode<U>> childs;
    private Set<U> uris;
    private TrieNode<U> parent;
    private int maxDepthOfChilds;

    TrieNode(String value, TrieNode<U> parent) {
        this.parent = parent;
        this.value = value;
        this.childs = new HashMap<>();
        this.uris = new HashSet<>();
        this.maxDepthOfChilds = 0;
    }

    public String getValue() {
        return value;
    }

    public Map<String, TrieNode<U>> getChilds() {
        return childs;
    }

    public void addUri(U uri) {
        uris.add(uri);
    }

    public Set<U> getUris() {
        return uris;
    }

    public int getMaxDepthOfChilds() {
        return maxDepthOfChilds;
    }

    private TrieNode<U> setMaxDepthOfChilds(int maxDepthOfChilds) {
        this.maxDepthOfChilds = maxDepthOfChilds;
        return this;
    }

    void updateMaxDepthOnAdd(int newDepth) {
        maxDepthOfChilds = Math.max(maxDepthOfChilds, newDepth);
        if (null == parent) {
            return;
        }
        if (parent.getMaxDepthOfChilds() < newDepth + 1) {
            parent.setMaxDepthOfChilds(newDepth + 1);
            parent.updateMaxDepthOnAdd(parent.getMaxDepthOfChilds());
        }
    }

    void udpdateMaxDepthOnDelete() {
        Collection<TrieNode<U>> childs = getChilds().values();
        if (childs.isEmpty()) {
            setMaxDepthOfChilds(0);
        } else {
            setMaxDepthOfChilds(Collections.max(childs.stream().map(i -> i.maxDepthOfChilds).collect(Collectors.toSet())) + 1);
        }
        if (null != parent) {
            parent.udpdateMaxDepthOnDelete();
        }
    }
}

public class Trie <U extends AbstractObjectUri> {

    private final Map<String, TrieNode<U>> roots;
    private final Function<String, String[]> splitter = i -> i.split("");

    public Trie() {
        this.roots = new HashMap<>();
    }

    public Collection<U> getUrisByStartsWith(String prefix) {
        String[] splitted = splitter.apply(prefix);
        Map<String, TrieNode<U>> localRoot = roots;
        TrieNode<U> lastProcessedNode = null;
        if (splitted.length == 0) {
            return Collections.emptySet();
        }
        for (String valueToSearch : splitted) {
            TrieNode<U> currentNode = localRoot.get(valueToSearch);
            if (currentNode == null) {
                return Collections.emptySet();
            }
            localRoot = currentNode.getChilds();
            lastProcessedNode = currentNode;
        }
        return lastProcessedNode.getUris();
    }

    public void addValueAndUri(String value, U uri) {
        Map<String, TrieNode<U>> localRoot = roots;
        String[] splitted = splitter.apply(value);
        if (splitted.length == 0) {
            return;
        }
        TrieNode<U> previousNode = null;
        for (String valueToAdd : splitted) {
            TrieNode<U> currentNode = localRoot.get(valueToAdd);
            if (currentNode == null) {
                currentNode = new TrieNode<>(valueToAdd, previousNode);
                currentNode.updateMaxDepthOnAdd(0);
                localRoot.put(valueToAdd, currentNode);
            }
            localRoot = currentNode.getChilds();
            currentNode.addUri(uri);
            previousNode = currentNode;
        }
    }

    public void removeUriFromTrie(U uri) {
        Map<String, TrieNode<U>> localRoot = roots;
        List<TrieNode<U>> foundNodes = new ArrayList<>();
        while (true) {
            TrieNode<U> node = null;
            for (TrieNode<U> nodeToCheck : localRoot.values()) {
                if (nodeToCheck.getUris().contains(uri)) {
                    node = nodeToCheck;
                    break;
                }
            }
            if (node == null) {
                break;
            }
            localRoot = node.getChilds();
            node.getUris().remove(uri);
            foundNodes.add(node);
        }
        wipeEmptyNodes(foundNodes);
    }

    private void wipeEmptyNodes(List<TrieNode<U>> foundNodes) {
        String followingValueToWipe = null;
        boolean needToWipeFollowing = false;
        for (int i = foundNodes.size() - 1; i >= 0; i--) {
            if (needToWipeFollowing && followingValueToWipe != null) {
                foundNodes.get(i).getChilds().remove(followingValueToWipe);
                foundNodes.get(i).udpdateMaxDepthOnDelete();
            }
            if (foundNodes.get(i).getUris().isEmpty()) {
                followingValueToWipe = foundNodes.get(i).getValue();
                needToWipeFollowing = true;
            }
        }
    }

    public int getMaxLength() {
        return roots.values().stream()
                .map(TrieNode::getMaxDepthOfChilds)
                .max(Integer::compare).get();
    }

}
