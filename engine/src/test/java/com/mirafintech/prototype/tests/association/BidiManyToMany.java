package com.mirafintech.prototype.tests.association;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BidiManyToMany {

/**
 * Bi-directional ManyToMany
 * - a third table is used to hold the association
 *
 * based on: https://vladmihalcea.com/the-best-way-to-use-the-manytomany-annotation-with-jpa-and-hibernate/
 * Post - owns the association
 *      - changes are only propagated to the database from Post's side
 */

@Getter
@Entity(name = "Post")
@Table(name = "post")
class Post {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany(cascade = {
            CascadeType.PERSIST, // no REMOVE - could trigger a chain deletion that would ultimately wipe both sides of the association
            CascadeType.MERGE
    })
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>(); // use @SortComparator + SortedSet if order it required

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }

    /**
     * uses id/pkey for equality
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        return id != null && id.equals(((Post) o).getId());
    }

    /**
     * fixed among all Post objects
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

@Entity(name = "Tag")
@Table(name = "tag")
@Getter
class Tag {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * mappedBy defined =>
     * - Post entity owns the association
     * - only one side can own a relationship
     * - changes are only propagated to the database from this particular side (owner's side)
     */
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    /**
     * equals + hashCode should be based on unique business fileds
     */
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Tag tag = (Tag) o;
//        return Objects.equals(this.<business key fields >, tag.<business key fields >);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash( < business key fields>);
//    }
}
}