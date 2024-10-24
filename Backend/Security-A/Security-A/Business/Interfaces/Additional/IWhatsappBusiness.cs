using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Business.Interfaces.Additional
{
    public interface IWhatsappBusiness
    {
        Task<string> EnviarMensajeAsync(string numeroWhatsApp, string template, string variable, string imageUrl);
    }
}
