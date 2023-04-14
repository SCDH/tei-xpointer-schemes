# Security

The XPointer processor will evaluate XPath expressions contained in
the pointer. This can lead to security issues, especially when
deployed on a server that is accessible from internet. Even when
running on your local machine, you should process only pointer
expressions that you trust to be non-malicious.

## The security risk

An XPath expression can be crafted to **read an arbitrary file** and
**send it to an arbitrary server** on the internet.

Using `unparsed-text()`, also non-XML files like `/etc/passwd` and
other system configurations can be read.

Unless you take counter measures, the XPath expression can be crafted
in a way, so that no exception occurs. Thus, you will not even notice,
that a file was stolen.

All files accessible with the privileges of the XPointer processors
system process are exposed to this thread.

## Counter measures

### Server

When deployed on a web server, take the following counter measures:

1. Allow access to documents via remote protocols like http(s) and
   ftp(s) only. Disallow access via the file protocol.

This can be achieved by setting restricting the protocols via the
Saxon configuration file.

```{xml}
<configuration xmlns="http://saxon.sf.net/ns/configuration"
    edition="HE"
    label="Hardened configuration">

  <global
      allowedProtocols="http,https,ftp,ftps"
      unparsedTextURIResolver="net.sf.saxon.lib.StandardUnparsedTextResolver"
      uriResolver="net.sf.saxon.lib.StandardURIResolver"
	  />

  <!-- ... -->

</configuration>
```

### Local machine

1. Unless you fully trust the XPath expressions in the processed
   XPointers, consider setting `allowedProtocols="file"` in the Saxon
   config file. This is the default setting in
   [`saxon/saxon-config.xml`](saxon/saxon-config.xml).

2. Also consider setting
   `uriResolver="org.teic.teixptr.implementation.security.DenyingURIResolver"`
   which serves an extra gatekeeper behind the closed door.


# When you find more security risks

When you found a security issue, please do not post it into the issue
tracker, but first report it via private mail to one of the developers
or to `scdh (at) uni-muenster (dot) de`.
