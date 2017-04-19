package wdsr.exercise3.record.rest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise3.record.Record;
import wdsr.exercise3.record.RecordInventory;

import java.util.ArrayList;
import java.util.List;

@Path("/records")
public class RecordResource {
	private static final Logger log = LoggerFactory.getLogger(RecordResource.class);
	
	@Inject
	private RecordInventory recordInventory;
	
	/**
	 * TODO Add methods to this class that will implement the REST API described below.
	 * Use methods on recordInventory to create/read/update/delete records. 
	 * RecordInventory instance (a CDI bean) will be injected at runtime.
	 * Content-Type used throughout this exercise is application/xml.
	 * API invocations that must be supported:
	 */
	
	/**
	 * * GET https://localhost:8091/records
	 * ** Returns a list of all records (as application/xml)
	 * ** Response status: HTTP 200
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getRecords(){
		List<Record> records = new ArrayList<>();
		records.addAll(recordInventory.getRecords());
		GenericEntity<List<Record>> genericResult = new GenericEntity<List<Record>>(records) {};
		return Response.ok(genericResult).build();
	}

	/**
	 * POST https://localhost:8091/records
	 * ** Creates a new record, returns ID of the new record.
	 * ** Consumes: record (as application/xml), ID must be null.
	 * ** Response status if ok: HTTP 201, Location header points to the newly created resource.
	 * ** Response status if submitted record has ID set: HTTP 400
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response addRecord(Record record, @Context UriInfo uriInfo){
		if(record.getId() != null){
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		recordInventory.addRecord(record);
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(Integer.toString(record.getId()));

		return Response.created(builder.build()).build();
	}

	/**
	 * * GET https://localhost:8091/records/{id}
	 * ** Returns an existing record (as application/xml)
	 * ** Response status if ok: HTTP 200
	 * ** Response status if {id} is not known: HTTP 404
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getRecord(@PathParam(value = "id") int id){
		Record record = recordInventory.getRecord(id);
		if(record == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		GenericEntity<Record> genericResult = new GenericEntity<Record>(record) {};
		return Response.ok(genericResult).build();
	}

	 /**
	 * * PUT https://localhost:8091/records/{id}
	 * ** Replaces an existing record in entirety.
	 * ** Submitted record (as application/xml) must have null ID or ID must be identical to {id} in the path.
	 * ** Response status if ok: HTTP 204
	 * ** Response status if {id} is not known: HTTP 404
	 */
	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response updateRecord(Record record,@PathParam(value = "id") int id){
		if (record.getId() != null && id != record.getId()) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Product ID is different in request path and message body").build();
		}
		if(recordInventory.updateRecord(id,record)){
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	/**
	 * * DELETE https://localhost:8091/records/{id}
	 * ** Deletes an existing record.
	 * ** Response status if ok: HTTP 204
	 * ** Response status if {id} is not known: HTTP 404
	 */
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Response deleteProduct(@PathParam(value = "id") int id) {
		if(recordInventory.deleteRecord(id)){
			return Response.noContent().build();
		}
		return Response.status(Response.Status.NOT_FOUND).build();
	}
}
