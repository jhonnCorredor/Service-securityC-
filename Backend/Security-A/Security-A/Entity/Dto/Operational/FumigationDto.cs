using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Operational
{
    public class FumigationDto
    {
        public int Id { get; set; }
        public DateTime DateFumigation { get; set; }
        public string QuantityMix { get; set; }
        public Boolean State { get; set; }
    }
}
