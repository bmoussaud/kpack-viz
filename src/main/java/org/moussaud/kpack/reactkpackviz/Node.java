package org.moussaud.kpack.reactkpackviz;

public class Node implements Comparable {
    private final String id;
    private final String kind;
    private final int group;
    private final boolean ready;

    public Node(String id, String kind, int group, boolean ready) {
        this.id = id;
        this.kind = kind;
        this.group = group;
        this.ready = ready;
    }

    public  Node(String id, String kind, int group) {
        this(id, kind, group, true);
    }

    public String getId() {
        return id;
    }

    public String getKind() {
        return kind;
    }

    public int getGroup() {
        return group;
    }

    public boolean isReady() {
        return ready;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + group;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((kind == null) ? 0 : kind.hashCode());
        result = prime * result + (ready ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (group != other.group)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (kind == null) {
            if (other.kind != null)
                return false;
        } else if (!kind.equals(other.kind))
            return false;
        if (ready != other.ready)
            return false;
        return true;
    }

    @Override
    public int compareTo(Object obj) {
        if (this == obj)
            return 0;
        if (obj == null)
            return 0;

        Node other = (Node) obj;

        return this.id.compareTo(other.getId());
    }

    @Override
    public String toString() {
        return "Node [group=" + group + ", id=" + id + ", kind=" + kind + ", ready=" + ready + "]";
    }

    
}
