package search.inmemory;

import objects.AbstractObjectUri;

import java.util.*;
import java.util.function.Function;

class TrieNode <U extends AbstractObjectUri> {

    private final String value;
    private Map<String, TrieNode<U>> childs;
    private Set<U> uris;

    TrieNode(String value) {
        this.value = value;
        this.childs = new HashMap<>();
        this.uris = new HashSet<>();
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
        for (String valueToAdd : splitted) {
            TrieNode<U> currentNode = localRoot.get(valueToAdd);
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
        for (String valueToAdd : splitted) {
            TrieNode<U> currentNode = localRoot.get(valueToAdd);
            if (currentNode == null) {
                currentNode = new TrieNode<>(valueToAdd);
                localRoot.put(valueToAdd, currentNode);
            }
            localRoot = currentNode.getChilds();
            currentNode.addUri(uri);
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
            }
            if (foundNodes.get(i).getUris().isEmpty()) {
                followingValueToWipe = foundNodes.get(i).getValue();
                needToWipeFollowing = true;
            }
        }
    }


}
