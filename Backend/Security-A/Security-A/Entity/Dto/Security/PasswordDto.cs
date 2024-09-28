using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Security
{
    public class PasswordDto
    {
        public int Id { get; set; }
        public int PersonId { get; set; }
        public string Code { get; set; }
    }
}
