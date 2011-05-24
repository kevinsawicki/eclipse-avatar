Eclipse Gravatar Plug-in
======

This is a plug-in to add [Gravatar](http://www.gravatar.com) support to Eclipse.

The org.avatar.github plug-in contains a persistent store of fetched Gravatar images
that can be used directly to get Image/ImageData instances for a specific hash or e-mail address.

This plug-in also contains LabelProvider and WorkbenchAdapter classes that provide images for
objects that can provide a hash.

![Avatar Example](/kevinsawicki/eclipse-avatar/raw/master/docs/avatar-example.png "GitHub view avatar screenshot")

Fetching an avatar from the plug-in store
------

```java
Avatar avatar = AvatarPlugin.getDefault().getAvatars().loadAvatarByEmail("name@example.com");
Image image = new AvatarImage(avatar).getScaledImage(32);
```

Getting a cached avatar from the plug-in store
------

```java
Avatar avatar = AvatarPlugin.getDefault().getAvatars().getAvatarByEmail("name@example.com");
Image image =  new AvatarImage(avatar).getScaledImage(32);
```

Creating a table viewer with Gravatar images
------

```java
TableViewer viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
viewer.setContentProvider(new ArrayContentProvider());
viewer.setLabelProvider(new AvatarLabelProvider(viewer));
//Replace with a valid e-mail address
viewer.setInput(new Object[] { "name@example.com" });
```

Other
------
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html)

As of May 14th, 2011 parts of this project have been donated to the [Mylyn](http://www.eclipse.org/mylyn/) Commons Identity API as part of [Bug 343602](https://bugs.eclipse.org/bugs/show_bug.cgi?id=343602).