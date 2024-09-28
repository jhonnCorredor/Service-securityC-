using Entity.Dto.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Business.Interfaces.Additional
{
    public interface IEmailBusiness
    {
        Task<bool> SendEmail(EmailDto request);
    }
}
