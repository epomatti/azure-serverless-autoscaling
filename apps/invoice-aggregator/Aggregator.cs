using Microsoft.Azure.WebJobs;
using Microsoft.Extensions.Logging;
using Microsoft.Azure.WebJobs.Extensions.DurableTask;

namespace invoice_aggregator
{
  public class Aggregator
  {
    [FunctionName("Invoice_Client")]
    public void Run([ServiceBusTrigger("invoice-authorized")] string message, [DurableClient] IDurableEntityClient client, ILogger log)
    {
      log.LogInformation($"C# ServiceBus queue trigger function processed message: {message}");
      var entityId = new EntityId(nameof(InvoiceEntity), message);
      client.SignalEntityAsync(entityId, "Add", 1);
    }

    [FunctionName("Invoice_Entity")]
    public static void InvoiceEntity([EntityTrigger] IDurableEntityContext ctx)
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
  }
}
