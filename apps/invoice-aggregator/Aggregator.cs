using Microsoft.Azure.WebJobs;
using Microsoft.Extensions.Logging;
using Microsoft.Azure.WebJobs.Extensions.DurableTask;
using Microsoft.Azure.WebJobs.Extensions.Http;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace invoice_aggregator
{
  public class Aggregator
  {
    [FunctionName("InvoiceClient")]
    public void Run([ServiceBusTrigger("invoice-authorized")] string message, [DurableClient] IDurableEntityClient client, ILogger log)
    {
      var entityId = new EntityId(nameof(InvoiceEntity), message);
      client.SignalEntityAsync(entityId, "Add", 1);
    }

    [FunctionName("InvoiceEntity")]
    public static void InvoiceEntity([EntityTrigger] IDurableEntityContext ctx, ILogger log)
    {
      switch (ctx.OperationName.ToLowerInvariant())
      {
        case "add":
          ctx.SetState(ctx.GetState<int>() + ctx.GetInput<int>());
          break;
        case "reset":
          ctx.SetState(0);
          break;
        case "get":
          ctx.Return(ctx.GetState<int>());
          break;
      }
    }

    [FunctionName("GetInvoice")]
    public static async Task<IActionResult> GetInvoice(
      [HttpTrigger(AuthorizationLevel.Anonymous, "get", Route = "InvoiceEntity/{entityKey}")] HttpRequest req,
      [DurableClient] IDurableEntityClient client,
      string entityKey,
      ILogger log)
    {
      var entityId = new EntityId(nameof(InvoiceEntity), entityKey);
      var state = await client.ReadEntityStateAsync<int>(entityId);
      return (ActionResult)new OkObjectResult(state);
    }
  }
}
