package online.zhaopei.myproject.jerseyservice;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import online.zhaopei.myproject.domain.ecssent.InvtHead;
import online.zhaopei.myproject.resource.DistResource;
import online.zhaopei.myproject.service.ecssent.UserUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import online.zhaopei.myproject.resource.InvtResource;
import online.zhaopei.myproject.service.ecssent.InvtHeadService;

@Component
@Path("/v1/invts")
@Consumes({MediaType.APPLICATION_JSON + ";charset=UTF-8", MediaType.TEXT_PLAIN + ";charset=UTF-8"})
@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
@Api(value = "清单相关接口", produces = "application/json")
public class InvtsEndpoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvtsEndpoint.class);

	@Autowired
	private InvtHeadService invtHeadService;

	@Autowired
	private UserUserService userUserService;
	
	/**@GET					//JAX-RS Annotation
	@ApiOperation(				//Swagger Annotation
			value = "根据关键字来查询清单", 
			response = InvtResource.class)  
	@ApiResponses(value = {		//Swagger Annotation
		@ApiResponse(code = 200, message = "成功", response = InvtResource.class),
	    @ApiResponse(code = 500, message = "服务器出错")
	})
	public Response login(@ApiParam(value = "关键字") @QueryParam("key") String key) {
		LOGGER.info("v1/login/{} - {key}", key, MediaType.APPLICATION_JSON);
		
		PageHelper.startPage(1, 10);
		List<InvtResource> invtResourceList = this.invtHeadService.getInvtList(key);
		return Response.ok(invtResourceList).header("Access-Control-Allow-Origin", "*").build();
	}*/

	@GET
	@ApiOperation(
			value = "根据企业代码，及密码还有清单号来判断该清单是否可以绑核放单",
			response = DistResource.class)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "成功", response = DistResource.class),
			@ApiResponse(code = 500, message = "服务器出错")})
    public Response isBindDist(@ApiParam(required = true, value = "企业代码") @QueryParam("companyCode") String companyCode,
							   @ApiParam(required = true, value = "密码") @QueryParam("password") String password,
							   @ApiParam(value = "电商平台代码") @QueryParam("ebpCode") String ebpCode,
							   @ApiParam(value = "订单号") @QueryParam("orderNo") String orderNo,
								@ApiParam(value = "运单号") @QueryParam("logisticsNo") String logisticsNo) {
		DistResource dr = new DistResource();
		InvtHead ih = null;
		int ihc = 0;
		List<InvtHead> ihList = null;
		if (StringUtils.isEmpty(companyCode) || StringUtils.isEmpty(password)
				|| (StringUtils.isEmpty(orderNo) && StringUtils.isEmpty(logisticsNo))) {
			dr.setStatus("2");
			dr.setInfo("companyCode,password,orderNo与logisticsNo不能同时为空,其他均不能为空，请检查参数!");
		} else {
		    if (0 == this.userUserService.countUserByLoginNameAndPassword(companyCode, password)) {
		    	dr.setStatus("2");
		    	dr.setInfo("企业代码或者密码不正确，请检查!");
			} else {
		    	ih = new InvtHead();
		    	ih.setEbpCode(ebpCode);
		    	ih.setOrderNo(orderNo);
		    	ih.setLogisticsNo(logisticsNo);
		    	ih.setSearchCompanyCode(companyCode);
		    	ih.setDistStat("2");
		    	ihc = this.invtHeadService.countInvtHead(ih);
		    	if (0 == ihc) {
					dr.setStatus("0");
					dr.setInfo("不可以绑");
				} else {
					dr.setStatus("1");
					dr.setInfo("可以绑");
				}
			}
		}
		return Response.ok(dr).header("Access-Control-Allow-Origin", "*").build();
	}
}