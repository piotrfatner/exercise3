package wdsr.exercise3.client;

import java.util.List;
import java.util.Set;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import wdsr.exercise3.model.Product;
import wdsr.exercise3.model.ProductType;
import wdsr.exercise3.server.IServerApplication;
import wdsr.exercise3.server.ProductResource;

public class ProductService extends RestClientBase {
	protected ProductService(final String serverHost, final int serverPort, final Client client) {
		super(serverHost, serverPort, client);
	}
	
	/**
	 * Looks up all products of given types known to the server.
	 * @param types Set of types to be looked up
	 * @return A list of found products - possibly empty, never null.
	 */
	public List<Product> retrieveProducts(Set<ProductType> types) {
		List<Product> products = baseTarget.path("/products").queryParam("type",types.toArray()).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Product>>(){});
		return products;
	}
	
	/**
	 * Looks up all products known to the server.
	 * @return A list of all products - possibly empty, never null.
	 */
	public List<Product> retrieveAllProducts() {
		List<Product> products = baseTarget.path("/products").request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<List<Product>>(){});
		return products;
	}
	
	/**
	 * Looks up the product for given ID on the server.
	 * @param id Product ID assigned by the server
	 * @return Product if found
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public Product retrieveProduct(int id) {
		Product product = baseTarget.path("/products/"+id).request(MediaType.APPLICATION_JSON_TYPE).get(new GenericType<Product>(){});
		if(product == null){
			throw new NotFoundException();
		}
		return product;
	}	
	
	/**
	 * Creates a new product on the server.
	 * @param product Product to be created. Must have null ID field.
	 * @return ID of the new product.
	 * @throws WebApplicationException if request to the server failed
	 */
	public int storeNewProduct(Product product) {
		if(product.getId() != null){
			throw new WebApplicationException();
		}
		Response response = baseTarget.path("/products").request().post(Entity.entity(product, MediaType.APPLICATION_JSON_TYPE),Response.class);
		String location = response.getLocation().toASCIIString();
		int id= Integer.parseInt(location.substring(location.length()-1));
		response.close();
		return id;
	}
	
	/**
	 * Updates the given product.
	 * @param product Product with updated values. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void updateProduct(Product product) {
		if(retrieveProduct(product.getId())== null){
			throw new NotFoundException();
		}
		WebTarget myPath = baseTarget.path("/products/"+product.getId());
		Response response = myPath.request(MediaType.APPLICATION_JSON).put(Entity.entity(product, MediaType.APPLICATION_JSON));

	}

	
	/**
	 * Deletes the given product.
	 * @param product Product to be deleted. Its ID must identify an existing resource.
	 * @throws NotFoundException if no product found for the given ID.
	 */
	public void deleteProduct(Product product) {
		if(retrieveProduct(product.getId())== null){
			throw new NotFoundException();
		}
		WebTarget myPath = baseTarget.path("/products/"+product.getId());
		Response response = myPath.request(MediaType.APPLICATION_JSON).delete(Response.class);
	}
}
