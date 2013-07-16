GeoJson POJOs for Jackson
=========================

A small package of all GeoJson POJOs (Plain Old Java Objects) for serializing and 
deserializing of objects via JSON Jackson Parser.

Usage
-----

If you know what kind of object you expect from a GeoJson file you can directly read it like this:

<code>
FeatureCollection featureCollection = new ObjectMapper().readValue(inputStream,FeatureCollection.class);
</code>

If you what to read any GeoJson file read it as GeoJsonObject and then test for the contents via instanceOf:

<code>
GeoJsonObject object = new ObjectMapper().readValue(inputStream,GeoJsonObject.class);
if (object instanceOf Polygon) {
	...
} else if (object instanceOf Feature) {
	...
}
</code>

and so on.

Maven Central
-------------

You can find the library in the maven central repository.

<code>
	<dependency>
		<groupId>de.grundid.opendatalab</groupId>
		<artifactId>geojson-jackson</artifactId>
		<version>1.0</version>
	</dependency>
</code>
		