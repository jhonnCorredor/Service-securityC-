using Entity.Model.Operational;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Operational
{
    public class FertilizationDto
    {
        public int Id { get; set; }
        public DateTime DateFertilization { get; set; }
        public string TypeFertilization { get; set; }
        public string QuantityMix { get; set; }
        public Boolean State { get; set; }
    }
}
