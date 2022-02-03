package org.moussaud.kpack.reactkpackviz;

public class Link {
    
    private final String source;
    private final String target;
    private final int value;

    public Link(String source, String target, int value) {
        this.source = source;
        this.target = target;
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + value;
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
        Link other = (Link) obj;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        if (value != other.value)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Link [source=" + source + ", target=" + target + ", value=" + value + "]";
    }

   
}
