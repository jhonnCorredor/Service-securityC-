using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Operational
{
    public class Fertilization
    {
        public int Id { get; set; }
        public DateTime DateFertilization { get; set; }
        public string TypeFertilization { get; set; }
        public string QuantityMix { get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
