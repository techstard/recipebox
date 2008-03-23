<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>

<html>
  <head>
    <title>xml actions</title>
  </head>
  <body>
    This example parses XML and uses an XPath expression...

    <p/>
    <c:set var="someXML">
      <people>
        <person>
          <name>Joe</name>
          <age>30</age>
        </person>
        <person>
          <name>Rosy</name>
          <age>29</age>
        </person>
      </people>
    </c:set>
    <x:parse var="parsedDocument" xml="${someXML}" />

    Here is a list of people:
    <ul>
      <x:forEach select="$parsedDocument/people/person">
        <li> <x:out select="name" /> </li>
      </x:forEach>
    </ul>
  </body>
</html>
